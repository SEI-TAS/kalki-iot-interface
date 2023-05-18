FROM python:2.7-alpine

# Trusted host configs used to avoid issues when running behind SSL proxies.
RUN pip config set global.trusted-host "pypi.org pypi.python.org files.pythonhosted.org"

# Installing Python deps without a venv (not needed in container).
COPY requirements.txt /plugins/wemo/
WORKDIR /plugins/wemo/
RUN apk --no-cache add build-base libffi-dev bash && \
    pip install -r requirements.txt --default-timeout=100 && \
    apk del build-base libffi-dev
RUN apk add --no-cache libstdc++

# API is listening here.
EXPOSE 7501

# Actual code.
COPY wemo.py /plugins/wemo/
COPY api.py /plugins/wemo/
COPY run.sh /plugins/wemo/

WORKDIR /plugins/wemo/
ENTRYPOINT ["bash", "run.sh"]
