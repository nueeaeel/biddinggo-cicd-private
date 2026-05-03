-- Adjust vector dimension if your embedding model is not 1536.
-- 과거 낙찰 완료 데이터를 예측 reference로 생성/갱신하는 upsert 함수.
-- 앱에서는 낙찰 완료 시점에 winner_price, condition_score, embedding을 함께 저장하면 된다.
create or replace function public.upsert_auction_price_reference(
    p_auction_id bigint,
    p_item_id bigint,
    p_category_id bigint,
    p_winner_price bigint,
    p_quality text,
    p_condition_score double precision,
    p_embedding vector(1536),
    p_embedding_model text,
    p_embedding_dimension integer,
    p_embedding_text text,
    p_completed_at timestamptz
)
returns public.auction_price_reference
language plpgsql
as $$
declare
    result_row public.auction_price_reference;
begin
    -- auction_id 기준으로 동일 낙찰 건이 중복 적재되지 않도록 upsert 처리한다.
    insert into public.auction_price_reference (
        auction_id,
        item_id,
        category_id,
        winner_price,
        quality,
        condition_score,
        embedding,
        embedding_model,
        embedding_dimension,
        embedding_text,
        completed_at
    ) values (
        p_auction_id,
        p_item_id,
        p_category_id,
        p_winner_price,
        p_quality,
        p_condition_score,
        p_embedding,
        p_embedding_model,
        p_embedding_dimension,
        p_embedding_text,
        p_completed_at
    )
    on conflict (auction_id)
    do update set
        item_id = excluded.item_id,
        category_id = excluded.category_id,
        winner_price = excluded.winner_price,
        quality = excluded.quality,
        condition_score = excluded.condition_score,
        embedding = excluded.embedding,
        embedding_model = excluded.embedding_model,
        embedding_dimension = excluded.embedding_dimension,
        embedding_text = excluded.embedding_text,
        completed_at = excluded.completed_at,
        updated_at = now()
    returning * into result_row;

    -- 저장 또는 갱신된 최종 레코드를 그대로 반환한다.
    return result_row;
end;
$$;
