UPDATE LAUNCHERS
SET TYPE  = 'web',
    MODEL = '{
               "branch":"master",
               "suite":"web"
            }'
WHERE NAME = 'Carina WEB';

UPDATE LAUNCHERS
SET TYPE  = 'api',
    MODEL = '{
               "platform":"API",
               "branch":"master",
               "suite":"api"
            }'
WHERE NAME = 'Carina API';