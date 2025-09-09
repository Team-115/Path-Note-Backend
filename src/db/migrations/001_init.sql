-- Extensions 활성화
CREATE EXTENSION IF NOT EXISTS vector;
CREATE EXTENSION IF NOT EXISTS pg_trgm;

-- 1. 장소 테이블
CREATE TABLE IF NOT EXISTS places (
    place_id BIGINT GENERATED ALWAYS AS IDENTITY,
    poi_id BIGINT NOT NULL,
    place_name VARCHAR(100) NOT NULL,
    place_category VARCHAR(50) NOT NULL,
    place_address VARCHAR(100) NOT NULL,
    place_coordinate_x DOUBLE PRECISION NOT NULL,
    place_coordinate_y DOUBLE PRECISION NOT NULL
);

-- 2. 코스 테이블
CREATE TABLE IF NOT EXISTS courses (
    course_id BIGINT GENERATED ALWAYS AS IDENTITY,
    course_name VARCHAR(100) NOT NULL,
    user_id BIGINT NOT NULL,
    course_description TEXT,
    course_category VARCHAR(50),
    course_image_url VARCHAR(255),
    course_vector VECTOR(768), -- 벡터 임베딩 저장용
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (course_id),
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 3. 코스-장소 매핑 테이블
CREATE TABLE IF NOT EXISTS course_places (
    course_place_id BIGINT GENERATED ALWAYS AS IDENTITY,
    course_id BIGINT NOT NULL,
    place_id BIGINT NOT NULL,
    sequence_index BIGINT NOT NULL,
    leave_time TIMESTAMP,
    enter_time TIMESTAMP,
    PRIMARY KEY (course_place_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (place_id) REFERENCES places(place_id) ON DELETE CASCADE
);

-- 4. 사용자 테이블
CREATE TABLE IF NOT EXISTS users (
    user_id BIGINT GENERATED ALWAYS AS IDENTITY,
    kakao_id VARCHAR(100) NOT NULL UNIQUE,
    nickname VARCHAR(50),
    profile_preset VARCHAR(10) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id)
);

-- 5. 코스 좋아요 테이블
CREATE TABLE IF NOT EXISTS likes (
    like_id BIGINT GENERATED ALWAYS AS IDENTITY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (like_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 6. 코스 댓글 테이블
CREATE TABLE IF NOT EXISTS comments (
    comment_id BIGINT GENERATED ALWAYS AS IDENTITY,
    course_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    content TEXT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (comment_id),
    FOREIGN KEY (course_id) REFERENCES courses(course_id) ON DELETE CASCADE,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE
);

-- 7. 장소 패턴 테이블
CREATE TABLE IF NOT EXISTS place_patterns (
    place_patterns_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    poi_ids INTEGER[] NOT NULL UNIQUE,  -- 장소 ID 배열 (순서 유지)
    pattern_embedding vector(768),

    -- 사용 통계
    sequence_count INTEGER DEFAULT 0,
    relation_count INTEGER DEFAULT 0,
    total_usage INTEGER GENERATED ALWAYS AS (sequence_count + relation_count) STORED,

    -- 점수
    sequence_score FLOAT DEFAULT 0.0,
    relation_score FLOAT DEFAULT 0.0,

    -- 메타데이터
    pattern_length SMALLINT GENERATED ALWAYS AS (array_length(poi_ids, 1)) STORED,
    first_poi INTEGER GENERATED ALWAYS AS (poi_ids[1]) STORED,
    last_poi INTEGER GENERATED ALWAYS AS (poi_ids[array_length(poi_ids, 1)]) STORED,

    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- 인덱스 생성
CREATE INDEX idx_places_category ON places(place_category);
CREATE INDEX idx_places_name_trgm ON places USING gin (place_name gin_trgm_ops);

-- 벡터 검색용 (pgvector)
CREATE INDEX idx_courses_vector ON courses USING ivfflat (course_vector vector_l2_ops) WITH (lists = 100);

--- 장소 패턴 최적화 인덱스
CREATE INDEX idx_place_patterns_length ON place_patterns(pattern_length);
CREATE INDEX idx_place_patterns_first_last ON place_patterns(first_poi, last_poi);
CREATE INDEX idx_place_patterns_poi_ids ON place_patterns USING gin (poi_ids);
CREATE INDEX idx_place_patterns_embedding ON place_patterns USING ivfflat (pattern_embedding vector_l2_ops) WITH (lists = 100);