#!/bin/bash
set -e

# Create new configset based off default one
cp -r /opt/solr/server/solr/configsets/_default/ /opt/solr/server/solr/configsets/"$ANNOTATION_INDEXING_CORE"/

# Overwrite Files copied to /opt/annotation-conf in Dockerfile
mv /opt/annotation-conf/* /opt/solr/server/solr/configsets/"$ANNOTATION_INDEXING_CORE"/conf/

# Set access rights for the configset
chown -R solr:solr /opt/solr/server/solr/configsets/"$ANNOTATION_INDEXING_CORE"

precreate-core "$ANNOTATION_INDEXING_CORE" /opt/solr/server/solr/configsets/"$ANNOTATION_INDEXING_CORE"

# Set access rights for the core data
chown -R solr:solr /opt/solr/server/solr/mycores/"$ANNOTATION_INDEXING_CORE"/

## drop access to solr and run cmd
exec gosu solr "$@"


