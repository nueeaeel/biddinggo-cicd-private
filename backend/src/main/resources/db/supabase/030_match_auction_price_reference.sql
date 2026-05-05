-- Adjust vector dimension if your embedding model is not 1536.
-- 예측 계산용 유사 낙찰 reference 검색 함수.
-- category_id는 필수 필터로 적용하고, condition_score는 후보군 제한용으로 사용한다.
-- 실제 정렬과 최종 유사도 계산의 중심은 embedding cosine similarity다.
create or replace function public.match_auction_price_reference(
    query_embedding vector(1536),
    filter_category_id bigint,
    min_condition_score double precision,
    max_condition_score double precision,
    match_count integer default 10,
    min_similarity double precision default 0,
    exclude_auction_id bigint default null
)
returns table (
    reference_id bigint,
    auction_id bigint,
    item_id bigint,
    category_id bigint,
    winner_price bigint,
    condition_score double precision,
    similarity double precision
)
language sql
stable
as $$
    -- similarity는 1 - cosine distance 로 계산한다.
    -- exclude_auction_id는 자기 자신이 reference 후보로 들어가는 것을 막기 위한 옵션이다.
    select
        apr.id as reference_id,
        apr.auction_id,
        apr.item_id,
        apr.category_id,
        apr.winner_price,
        apr.condition_score,
        1 - (apr.embedding <=> query_embedding) as similarity
    from public.auction_price_reference apr
    where apr.category_id = filter_category_id
      and apr.condition_score between min_condition_score and max_condition_score
      and (exclude_auction_id is null or apr.auction_id <> exclude_auction_id)
      and 1 - (apr.embedding <=> query_embedding) >= min_similarity
    order by apr.embedding <=> query_embedding
    limit greatest(match_count, 0);
$$;
