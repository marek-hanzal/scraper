FROM docker.leight.rocks/tools/buffalo

COPY --chown=app:app ./dist /opt/app
