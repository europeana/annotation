# Please note, this file should have unix-based end of line
FROM library/solr:7.7

# copy custom config settings
COPY conf/ /opt/annotation-conf/

COPY solr-entrypoint.sh /solr-entrypoint.sh

# switch to root, let the entrypoint drop back to solr
USER root
RUN chmod +x /solr-entrypoint.sh
ENTRYPOINT ["/solr-entrypoint.sh"]
CMD ["solr", "-f"]