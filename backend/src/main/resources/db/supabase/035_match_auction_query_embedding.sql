-- Adjust vector dimension if your embedding model is not 1536.
-- 검색어 임베딩을 기준으로 유사한 경매 query embedding 후보를 검색하는 함수.
-- 카테고리 필터와 최종 정렬은 적용하지 않고, 후보 추출만 담당한다.
create or replace function public.match_auction_query_embedding(
    query_embedding vector(1536),
    match_count integer default 100,
    min_similarity double precision default 0
)
returns table (
    auction_id bigint,
    item_id bigint,
    category_id bigint
)
language sql
stable
as $$
    -- 최종 정렬은 애플리케이션에서 최신순으로 다시 적용하므로,
    -- 여기서는 유사한 후보를 충분히 뽑아주는 역할만 수행한다.
    select
        aqe.auction_id,
        aqe.item_id,
        aqe.category_id
    from public.auction_query_embedding aqe
    where 1 - (aqe.embedding <=> query_embedding) >= min_similarity
    order by aqe.embedding <=> query_embedding
    limit greatest(match_count, 0);
$$;
