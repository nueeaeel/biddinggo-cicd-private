-- Adjust vector dimension if your embedding model is not 1536.
-- 과거 낙찰 완료 데이터를 예측 reference로 저장하는 테이블.
-- 예측가 계산 시 category_id, condition_score, embedding 유사도를 함께 활용한다.
create table if not exists public.auction_price_reference (
    id bigserial primary key,
    auction_id bigint not null unique,
    item_id bigint not null,
    category_id bigint not null,
    winner_price bigint not null check (winner_price >= 0),
    quality text null,
    condition_score double precision not null check (condition_score >= 0 and condition_score <= 1),
    embedding vector(1536) not null,
    embedding_model text not null,
    embedding_dimension integer not null default 1536,
    embedding_text text not null,
    completed_at timestamptz not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_auction_price_reference_category_id
    on public.auction_price_reference (category_id);

-- 카테고리 + condition_score 범위 필터를 빠르게 처리하기 위한 인덱스.
create index if not exists idx_auction_price_reference_category_condition
    on public.auction_price_reference (category_id, condition_score);

-- 최근 낙찰 데이터 확인 및 운영성 점검을 위한 보조 인덱스.
create index if not exists idx_auction_price_reference_completed_at
    on public.auction_price_reference (completed_at desc);

-- cosine similarity 검색 성능 향상을 위한 벡터 인덱스.
create index if not exists idx_auction_price_reference_embedding
    on public.auction_price_reference
    using hnsw (embedding vector_cosine_ops);

-- 레코드 수정 시 updated_at이 자동 갱신되도록 트리거를 연결한다.
drop trigger if exists trg_auction_price_reference_updated_at on public.auction_price_reference;

create trigger trg_auction_price_reference_updated_at
before update on public.auction_price_reference
for each row
execute function public.set_current_timestamp_updated_at();
