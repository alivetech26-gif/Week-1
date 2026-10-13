package com.skillnexus.blog;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@SpringBootApplication
public class BlogApiApplication {
    public static void main(String[] args) { SpringApplication.run(BlogApiApplication.class, args); }

    @Bean PasswordEncoder passwordEncoder() { return new BCryptPasswordEncoder(); }

    @RestControllerAdvice
    static class Errors {
        @ExceptionHandler(IllegalArgumentException.class)
        ResponseEntity<Map<String,String>> badRequest(IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
        @ExceptionHandler(NoSuchElementException.class)
        ResponseEntity<Map<String,String>> missing(NoSuchElementException e) {
            return ResponseEntity.status(404).body(Map.of("error", e.getMessage()));
        }
        @ExceptionHandler(SecurityException.class)
        ResponseEntity<Map<String,String>> denied(SecurityException e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }

    record RegisterRequest(@NotBlank @Size(max=40) String username, @NotBlank @Size(min=8,max=100) String password) {}
    record LoginRequest(@NotBlank String username, @NotBlank String password) {}
    record PostRequest(@NotBlank @Size(max=150) String title, @NotBlank @Size(max=10000) String content) {}
    record CommentRequest(@NotBlank @Size(max=2000) String content) {}
    record UserView(String username, String token) {}
    record Comment(long id, String username, String content, Instant createdAt) {}
    record PostView(long id, String author, String title, String content, Set<String> likes, List<Comment> comments, Instant createdAt) {}

    static class Post {
        long id; String author; String title; String content; Set<String> likes = ConcurrentHashMap.newKeySet();
        List<Comment> comments = Collections.synchronizedList(new ArrayList<>()); Instant createdAt = Instant.now();
        PostView view() { synchronized(comments) { return new PostView(id,author,title,content,Set.copyOf(likes),List.copyOf(comments),createdAt); } }
    }

    @RestController
    @RequestMapping("/api")
    static class BlogController {
        private final PasswordEncoder encoder;
        private final String secret;
        private final Map<String,String> users = new ConcurrentHashMap<>();
        private final Map<Long,Post> posts = new ConcurrentHashMap<>();
        private final AtomicInteger postIds = new AtomicInteger();
        private final AtomicInteger commentIds = new AtomicInteger();
        BlogController(PasswordEncoder encoder, @Value("${app.jwt.secret}") String secret) { this.encoder=encoder; this.secret=secret; }

        @PostMapping("/auth/register")
        ResponseEntity<UserView> register(@Valid @RequestBody RegisterRequest request) {
            if (users.putIfAbsent(request.username().trim(), encoder.encode(request.password())) != null)
                throw new IllegalArgumentException("Username already exists.");
            String name=request.username().trim();
            return ResponseEntity.status(201).body(new UserView(name, token(name)));
        }

        @PostMapping("/auth/login")
        UserView login(@Valid @RequestBody LoginRequest request) {
            String hash=users.get(request.username());
            if(hash==null || !encoder.matches(request.password(),hash)) throw new SecurityException("Invalid username or password.");
            return new UserView(request.username(),token(request.username()));
        }

        @PostMapping("/posts")
        ResponseEntity<PostView> create(@RequestHeader(value="Authorization",required=false) String auth,@Valid @RequestBody PostRequest request) {
            String user=authenticate(auth); Post p=new Post(); p.id=postIds.incrementAndGet(); p.author=user; p.title=request.title().trim(); p.content=request.content().trim(); posts.put(p.id,p);
            return ResponseEntity.status(201).body(p.view());
        }

        @GetMapping("/posts")
        Map<String,Object> list(@RequestParam(defaultValue="0") int page,@RequestParam(defaultValue="10") int size,@RequestParam(required=false) String search) {
            if(page<0 || size<1 || size>100) throw new IllegalArgumentException("Page must be >= 0 and size must be between 1 and 100.");
            List<PostView> all=posts.values().stream().filter(p -> search==null || (p.title+" "+p.content).toLowerCase(Locale.ROOT).contains(search.toLowerCase(Locale.ROOT))).sorted(Comparator.comparingLong((Post p)->p.id).reversed()).map(Post::view).toList();
            int from=Math.min(page*size,all.size()), to=Math.min(from+size,all.size());
            return Map.of("page",page,"size",size,"total",all.size(),"items",all.subList(from,to));
        }

        @GetMapping("/posts/{id}") PostView get(@PathVariable long id) { return post(id).view(); }

        @PutMapping("/posts/{id}")
        PostView update(@RequestHeader(value="Authorization",required=false) String auth,@PathVariable long id,@Valid @RequestBody PostRequest request) {
            String user=authenticate(auth); Post p=post(id); owner(p,user); p.title=request.title().trim(); p.content=request.content().trim(); return p.view();
        }

        @DeleteMapping("/posts/{id}") ResponseEntity<Void> delete(@RequestHeader(value="Authorization",required=false) String auth,@PathVariable long id) {
            String user=authenticate(auth); Post p=post(id); owner(p,user); posts.remove(id); return ResponseEntity.noContent().build();
        }

        @PostMapping("/posts/{id}/comments")
        ResponseEntity<Comment> comment(@RequestHeader(value="Authorization",required=false) String auth,@PathVariable long id,@Valid @RequestBody CommentRequest request) {
            String user=authenticate(auth); Post p=post(id); Comment c=new Comment(commentIds.incrementAndGet(),user,request.content().trim(),Instant.now()); p.comments.add(c); return ResponseEntity.status(201).body(c);
        }

        @PostMapping("/posts/{id}/likes")
        Map<String,Object> like(@RequestHeader(value="Authorization",required=false) String auth,@PathVariable long id) {
            String user=authenticate(auth); Post p=post(id); boolean liked=p.likes.add(user); if(!liked)p.likes.remove(user);
            return Map.of("postId",id,"liked",liked,"likeCount",p.likes.size());
        }

        private Post post(long id) { Post p=posts.get(id); if(p==null)throw new NoSuchElementException("Post not found."); return p; }
        private void owner(Post p,String user) { if(!p.author.equals(user))throw new SecurityException("Only the post author can modify this post."); }
        private String authenticate(String header) {
            if(header==null || !header.startsWith("Bearer "))throw new SecurityException("Provide a Bearer token.");
            String[] parts=header.substring(7).split("\\.",-1);
            try {
                if(parts.length!=3 || !parts[0].equals(Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8)))) throw new SecurityException("Invalid token.");
                String payload=new String(Base64.getUrlDecoder().decode(parts[1]),StandardCharsets.UTF_8);
                String expected=sign(parts[0]+"."+parts[1]);
                if(!java.security.MessageDigest.isEqual(expected.getBytes(StandardCharsets.UTF_8),parts[2].getBytes(StandardCharsets.UTF_8)))throw new SecurityException("Invalid token signature.");
                String username=payload.replaceAll(".*\"sub\":\"([^\"]+)\".*","$1");
                long exp=Long.parseLong(payload.replaceAll(".*\"exp\":([0-9]+).*","$1"));
                if(exp<Instant.now().getEpochSecond() || !users.containsKey(username))throw new SecurityException("Token expired or user no longer exists.");
                return username;
            } catch (IllegalArgumentException e) { throw new SecurityException("Invalid token."); }
        }
        private String token(String username) {
            String header=Base64.getUrlEncoder().withoutPadding().encodeToString("{\"alg\":\"HS256\",\"typ\":\"JWT\"}".getBytes(StandardCharsets.UTF_8));
            String payload=Base64.getUrlEncoder().withoutPadding().encodeToString(("{\"sub\":\""+username.replace("\\","").replace("\"","")+"\",\"exp\":"+Instant.now().plusSeconds(86400).getEpochSecond()+"}").getBytes(StandardCharsets.UTF_8));
            String unsigned=header+"."+payload; return unsigned+"."+sign(unsigned);
        }
        private String sign(String data) {
            try { Mac mac=Mac.getInstance("HmacSHA256"); mac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8),"HmacSHA256")); return Base64.getUrlEncoder().withoutPadding().encodeToString(mac.doFinal(data.getBytes(StandardCharsets.UTF_8))); }
            catch(Exception e) { throw new IllegalStateException("Could not sign token.",e); }
        }
    }

    @Configuration
    static class RateLimitConfiguration {
        @Bean RateLimitFilter rateLimitFilter() { return new RateLimitFilter(); }
    }
    static class RateLimitFilter extends OncePerRequestFilter {
        private final Map<String,Window> windows=new ConcurrentHashMap<>();
        static class Window { long start=System.currentTimeMillis(); int count; }
        @Override protected void doFilterInternal(HttpServletRequest req,HttpServletResponse res,FilterChain chain) throws ServletException,IOException {
            String key=req.getRemoteAddr(); long now=System.currentTimeMillis();
            Window w=windows.compute(key,(k,old)->old==null || now-old.start>=60000 ? new Window() : old);
            synchronized(w) {
                if(++w.count>120) { res.setStatus(429); res.setContentType("application/json"); res.getWriter().write("{\"error\":\"Rate limit exceeded. Try again in a minute.\"}"); return; }
            }
            res.setHeader("X-RateLimit-Limit","120"); chain.doFilter(req,res);
        }
    }
}
