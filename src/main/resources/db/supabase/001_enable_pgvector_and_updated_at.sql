-- Supabase에서 벡터 검색을 사용하기 위한 pgvector 확장을 활성화한다.
-- 이후 생성되는 embedding 컬럼과 cosine similarity 검색 함수의 전제 조건이다.
create extension if not exists vector;

-- updated_at 자동 갱신을 위한 공용 트리거 함수.
-- auction_query_embedding, auction_price_reference 테이블에서 함께 재사용한다.
create or replace function public.set_current_timestamp_updated_at()
returns trigger
language plpgsql
as $$
begin
    new.updated_at = now();
    return new;
end;
$$;
