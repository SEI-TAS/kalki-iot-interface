FROM python:2.7-alpine

RUN pip install pipenv==2018.11.26

# Installing Python deps without a venv (not needed in container).
COPY Pipfile /plugins/wemo/
COPY Pipfile.lock /plugins/wemo/
WORKDIR /plugins/wemo/
RUN apk --no-cache add build-base libffi-dev bash && \
    pipenv install --system --deploy --ignore-pipfile && \
    apk del build-base libffi-dev

# API is listening here.
EXPOSE 7501

# Actual code.
COPY wemo.py /plugins/wemo/
COPY api.py /plugins/wemo/
COPY run.sh /plugins/wemo/

WORKDIR /plugins/wemo/
ENTRYPOINT ["bash", "run.sh"]
