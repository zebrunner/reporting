SELECT cron.schedule ('0 0 1 * *', $$REFRESH MATERIALIZED VIEW CONCURRENTLY zafira.YEAR_MATERIALIZED$$);
SELECT cron.schedule ('10 0 1 1 *', $$REFRESH MATERIALIZED VIEW CONCURRENTLY zafira.TOTAL_MATERIALIZED$$);
