-- Adjust vector dimension if your embedding model is not 1536.
-- 조회 대상 경매의 임베딩을 생성/갱신하는 upsert 함수.
-- 앱에서는 경매 등록/수정 후 이 함수를 호출해 최신 임베딩을 유지하면 된다.
create or replace function public.upsert_auction_query_embedding(
    p_auction_id bigint,
    p_item_id bigint,
    p_category_id bigint,
    p_embedding vector(1536),
    p_embedding_model text,
    p_embedding_dimension integer,
    p_embedding_text text
)
returns public.auction_query_embedding
language plpgsql
as $$
declare
    result_row public.auction_query_embedding;
begin
    -- auction_id 기준으로 한 경매당 하나의 최신 임베딩만 유지한다.
    insert into public.auction_query_embedding (
        auction_id,
        item_id,
        category_id,
        embedding,
        embedding_model,
        embedding_dimension,
        embedding_text
    ) values (
        p_auction_id,
        p_item_id,
        p_category_id,
        p_embedding,
        p_embedding_model,
        p_embedding_dimension,
        p_embedding_text
    )
    on conflict (auction_id)
    do update set
        item_id = excluded.item_id,
        category_id = excluded.category_id,
        embedding = excluded.embedding,
        embedding_model = excluded.embedding_model,
        embedding_dimension = excluded.embedding_dimension,
        embedding_text = excluded.embedding_text,
        updated_at = now()
    returning * into result_row;

    -- 저장 또는 갱신된 최종 레코드를 그대로 반환한다.
    return result_row;
end;
$$;
