-- Adjust vector dimension if your embedding model is not 1536.
-- 현재 경매 상세 조회 대상의 임베딩을 저장하는 테이블.
-- 조회 시 즉석 임베딩을 만들지 않고, 등록/수정 시 준비한 임베딩을 재사용하기 위한 저장소다.
create table if not exists public.auction_query_embedding (
    id bigserial primary key,
    auction_id bigint not null unique,
    item_id bigint not null unique,
    category_id bigint not null,
    embedding vector(1536) not null,
    embedding_model text not null,
    embedding_dimension integer not null default 1536,
    embedding_text text not null,
    created_at timestamptz not null default now(),
    updated_at timestamptz not null default now()
);

create index if not exists idx_auction_query_embedding_category_id
    on public.auction_query_embedding (category_id);

-- 최신 임베딩 기준 운영 확인을 쉽게 하기 위한 보조 인덱스.
create index if not exists idx_auction_query_embedding_updated_at
    on public.auction_query_embedding (updated_at desc);

-- cosine similarity 검색 성능 향상을 위한 벡터 인덱스.
create index if not exists idx_auction_query_embedding_embedding
    on public.auction_query_embedding
    using hnsw (embedding vector_cosine_ops);

-- 레코드 수정 시 updated_at이 자동 갱신되도록 트리거를 연결한다.
drop trigger if exists trg_auction_query_embedding_updated_at on public.auction_query_embedding;

create trigger trg_auction_query_embedding_updated_at
before update on public.auction_query_embedding
for each row
execute function public.set_current_timestamp_updated_at();
