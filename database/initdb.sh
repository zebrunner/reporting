#@/bin/bash

file="initdb.d/1-init.sh"

echo "#!/bin/bash" > $file
echo "" >> $file

echo "if [ "\$\( psql -v ON_ERROR_STOP=1 --username \$POSTGRES_USER" -tAc \"SELECT 1 FROM pg_namespace WHERE nspname = 'zafira'\" ) = '1' ]; then" >> $file
echo "echo \"Schema already exists\"" >> $file
echo "exit 1" >> $file
echo "fi" >> $file
echo "" >> $file

echo "psql -v ON_ERROR_STOP=1 --username "\$POSTGRES_USER" <<-EOSQL" >> $file
while IFS= read -r line;do
    echo "$line" | sed -e 's#\$#\\$#g' >> $file
done < "db-pg.sql"
echo "" >> $file
while IFS= read -r line;do
    echo "$line" | sed -e 's#\$#\\$#g' >> $file
done < "db-data.sql"
echo "EOSQL" >> $file

chmod a+x $file
