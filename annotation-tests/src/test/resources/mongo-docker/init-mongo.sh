# This script creates the Entity database and the Spring Batch JobRepository
mongo -- "$MONGO_INITDB_DATABASE" <<EOF
    var rootUser = '$MONGO_INITDB_ROOT_USERNAME';
    var rootPassword = '$MONGO_INITDB_ROOT_PASSWORD';

    db.auth(rootUser, rootPassword);

db.getSiblingDB('$ANNOTATION_DB').createCollection('annotation');

EOF