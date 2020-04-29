FROM python:3.8-alpine

RUN pip install pipenv

# Installing Python deps without a venv (not needed in container).
COPY Pipfile /plugins/wemo/
COPY Pipfile.lock /plugins/wemo/
WORKDIR /plugins/wemo/
RUN apk --no-cache add build-base libffi-dev  && \
    pipenv install --system --deploy --ignore-pipfile && \
    apk del build-base libffi-dev

# Actual code.
COPY wemo.py /plugins/wemo/
COPY api.py /plugins/wemo/

WORKDIR /plugins/wemo/
ENTRYPOINT ["python", "wemo.py"]
