# This script creates the annotation database and allows the given user to authenticate
# The root user is created because of setting the given env variables (e.g. MONGO_INITDB_ROOT_USERNAME), and it is automatically placed in the "admin" db
# The script schould have a unix end of line
# in case of errors use notepad++ to change EOL to unix LF (edit -> EOL Conversion)
mongo -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = '$MONGO_INITDB_ROOT_USERNAME';
    var rootPassword = '$MONGO_INITDB_ROOT_PASSWORD';

    db.auth(rootUser, rootPassword);

db.getSiblingDB('$ANNOTATION_DB').createCollection('annotation');

EOF