# BUILD IMAGE WITH:
# docker build -t ploc_build --build-arg GOZER_ADDRESS_VAR="http://<GOZER ADDRESS>:<PORT>" .
#
# RUN CONTAINER WITH:
# docker run --name ploc --rm -v ~[PATH TO OUTPUT DIR]:/usr/local/ploc-app/output/ ploc_build

FROM gradle:5.1.1-jdk8

# not root before this
USER root

# build-arg
ARG GOZER_ADDRESS_VAR

# define variables for android SDK download and path
ENV SDK_URL="https://dl.google.com/android/repository/sdk-tools-linux-4333796.zip" \
    ANDROID_HOME="/usr/local/android-sdk" \
    PLOC_DIR="/usr/local/ploc-app" \
    GOZER_ADDRESS=\"$GOZER_ADDRESS_VAR\"
    
# create android SDK folder, download SDK, unzip, remove zip file and accept all licences
RUN mkdir -p ${ANDROID_HOME} && \
    cd ${ANDROID_HOME} && \
    wget ${SDK_URL} -O android_sdk.zip && \
    unzip android_sdk.zip && \
    rm android_sdk.zip && \
    yes | $ANDROID_HOME/tools/bin/sdkmanager --licenses

# create ploc application directory and clone the source code from the git repo
RUN mkdir -p ${PLOC_DIR} && \
    cd ${PLOC_DIR} && \
    git clone -b develop https://github.com/fzi-forschungszentrum-informatik/dream-ploc.git

WORKDIR ${PLOC_DIR}

# pull source from git, make gradlew executable, build the apk and copy apk to mounted volume
CMD cd dream-ploc && \ 
    git pull https://github.com/fzi-forschungszentrum-informatik/dream-ploc.git && \
    chmod +x ./gradlew && \
    # Build PATH: /usr/local/ploc-app/ploc/app/build/outputs/apk/debug/app-debug.apk
    ./gradlew clean assembleDebug -PGOZER_ADDRESS=GOZER_ADDRESS_VAR && \
    cp /usr/local/ploc-app/dream-ploc/app/build/outputs/apk/debug/app-debug.apk /usr/local/ploc-app/output/ploc.apk
