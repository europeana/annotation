#!/bin/sh

# Decrypt the file
mkdir -p /opt/app/config
# --batch to prevent interactive command
# --yes to assume "yes" for questions
gpg --quiet --batch --yes --decrypt --passphrase="$SECRET_PROPS_PASSPHRASE" \
--output /opt/app/config/annotation.user.properties ./.github/workflows/annotation.user.properties.gpg