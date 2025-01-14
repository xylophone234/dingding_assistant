FROM gitpod/workspace-full

USER gitpod

# 安装 Java 11
RUN bash -c ". /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    sdk install java 11.0.12-open && \
    sdk default java 11.0.12-open"

# 设置 JAVA_HOME 环境变量
ENV JAVA_HOME=/home/gitpod/.sdkman/candidates/java/current
ENV PATH=${JAVA_HOME}/bin:${PATH}

# 安装 Android SDK
RUN mkdir -p /home/gitpod/android-sdk && \
    wget -q https://dl.google.com/android/repository/commandlinetools-linux-8512546_latest.zip -O /tmp/sdk.zip && \
    unzip -q /tmp/sdk.zip -d /home/gitpod/android-sdk/cmdline-tools && \
    mv /home/gitpod/android-sdk/cmdline-tools/cmdline-tools /home/gitpod/android-sdk/cmdline-tools/latest && \
    rm /tmp/sdk.zip

ENV ANDROID_HOME=/home/gitpod/android-sdk
ENV PATH=${PATH}:${ANDROID_HOME}/cmdline-tools/latest/bin:${ANDROID_HOME}/platform-tools

# 安装必要的 Android SDK 组件
RUN bash -c "source /home/gitpod/.sdkman/bin/sdkman-init.sh && \
    yes | sdkmanager --licenses && \
    sdkmanager 'platform-tools' 'platforms;android-33' 'build-tools;33.0.2'"
