#! /bin/bash
ENV_FILE=env.txt
SEPARATOR_LOG="------------------------"
touch $ENV_FILE
mkdir -p ../../runs/
mkdir -p log

echo `date` >> $ENV_FILE
echo $SEPARATOR_LOG >> $ENV_FILE

sudo apt update
#sudo apt upgrade -y
sudo apt install -y build-essential libz-dev zlib1g-dev unzip zip

# generic info
uname -r -p >> $ENV_FILE
echo $SEPARATOR_LOG >> $ENV_FILE

# sdk-man
curl -s "https://get.sdkman.io" | bash
source "$HOME/.sdkman/bin/sdkman-init.sh"

# java graalvm
sdk install java 21.1.0.r11-grl
echo 'JAVA GRAALVM VERSION' >> $ENV_FILE
sdk use java 21.1.0.r11-grl
java -version &>> $ENV_FILE
echo $SEPARATOR_LOG >> $ENV_FILE
# install native image
gu install native-image
if [[ -z "${GRAALVM_HOME}" ]]; then
  echo "export GRAALVM_HOME=$HOME/.sdkman/candidates/java/21.1.0.r11-grl/" >> $HOME/.bashrc
  source "$HOME/.bashrc"
fi

# java adoptopenjdk
sdk install java 11.0.11.hs-adpt
echo 'JAVA ADOPTOPENJDK HOTSPOT VERSION' >> $ENV_FILE
sdk use java 11.0.11.hs-adpt
java -version &>> $ENV_FILE
echo $SEPARATOR_LOG >> $ENV_FILE

# java adoptopenjdk openj9
sdk install java 11.0.11.j9-adpt
echo 'JAVA ADOPTOPENJDK OPENJ9 VERSION' >> $ENV_FILE
sdk use java 11.0.11.j9-adpt
java -version &>> $ENV_FILE
echo $SEPARATOR_LOG >> $ENV_FILE

# java openjdk
sdk install java 11.0.11-open
echo 'JAVA OPENJDK VERSION' >> $ENV_FILE
sdk use java 11.0.11-open
java -version &>> $ENV_FILE
echo $SEPARATOR_LOG >> $ENV_FILE


# LD PRELOAD
# I suggest forking the original https://github.com/airlift/procname
# as I don't know if I'll change or delete my fork (or the original author does it)
cd ..
mkdir -p utils
cd utils
git clone https://github.com/Kraktun/procname
cd procname
make

echo $SEPARATOR_LOG >> $ENV_FILE
