# Week 3 System Design Tasks

## 1. URL Shortener Architecture

### API endpoints
- `POST /shorten`: accept a validated long URL and return a short code and short URL.
- `GET /{code}`: look up the code and redirect to the original URL.
- `GET /urls/{code}/stats`: return creation time and redirect count for an authenticated owner.

### Architecture
1. API gateway/load balancer distributes requests and applies request limits.
2. URL service validates HTTP/HTTPS URLs, generates a collision-checked random Base62 code, and stores the mapping.
3. Redis caches popular code-to-URL mappings; the database remains the source of truth.
4. A relational database stores short code, destination URL, owner, creation time, optional expiry, and click count.
5. Analytics events can be sent to a queue for asynchronous reporting.

### Scale and reliability
Use a unique index on the short code, retry on collisions, cache hot mappings, and use database backups and monitoring. A redirect should return HTTP 302 by default so destinations can be changed later. Rate-limit URL creation to reduce abuse and validate destinations to reduce phishing/open-redirect risks.

## 2. Social Media Database Schema (normalised)

```sql
CREATE TABLE users (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  username VARCHAR(40) NOT NULL UNIQUE,
  email VARCHAR(254) NOT NULL UNIQUE,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE posts (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  author_id BIGINT NOT NULL REFERENCES users(id),
  title VARCHAR(150) NOT NULL,
  body TEXT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE comments (
  id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
  author_id BIGINT NOT NULL REFERENCES users(id),
  body VARCHAR(2000) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE post_likes (
  user_id BIGINT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
  post_id BIGINT NOT NULL REFERENCES posts(id) ON DELETE CASCADE,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (user_id, post_id)
);

CREATE INDEX idx_posts_created_at ON posts(created_at DESC);
CREATE INDEX idx_posts_author ON posts(author_id);
CREATE INDEX idx_comments_post ON comments(post_id, created_at);
```

The separate `post_likes` table prevents duplicate likes and represents the many-to-many relationship between users and posts. Passwords must be stored as strong hashes, never plaintext.
