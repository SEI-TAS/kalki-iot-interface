sudo apt-get -yqq install python python-pip
sudo python -m pip install pipenv

mkdir -p ../../temp/wemo
cp wemo.py ../../temp/wemo/
cp Pipfile ../../temp/wemo/

cd ../../temp/wemo
ENV PIPENV_VENV_IN_PROJECT "enabled"
pipenv install
