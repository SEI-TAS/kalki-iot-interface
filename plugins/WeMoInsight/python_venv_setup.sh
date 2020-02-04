sudo pip install virtualenv
mkdir ouimeaux-env
virtualenv ouimeaux-env
source ouimeaux-env/bin/activate
cd ouimeaux-env && pip install git+https://github.com/iancmcc/ouimeaux.git
