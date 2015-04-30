# mvn-p2-dependencies

This will list down all the feature dependencies a given p2 feature have, by searching through the specified local maven repo.

## Usage
    java -jar lib/mvn-p2-dependencies-1.0-SNAPSHOT-jar-with-dependencies.jar -repo /Users/ruchira/.m2/repository -id org.wso2.store -version 2.1.0.SNAPSHOT
## Output
    cannot find org.wso2.carbon.core.ui:4.4.0
    cannot find org.wso2.carbon.core:4.4.0
    cannot find org.wso2.carbon.social:1.3.0.SNAPSHOT
    org.jaggeryjs.modules.handlebars:1.0.0
    org.wso2.store.modules.ues:2.1.0.SNAPSHOT
    org.wso2.store.modules.store:2.1.0.SNAPSHOT
    org.jaggeryjs.modules.sso:1.2.2
    org.wso2.carbon.core:4.4.0
    org.jaggeryjs.server:0.10.0
    org.wso2.store.modules.permission:2.1.0.SNAPSHOT
    org.wso2.carbon.social:1.3.0.SNAPSHOT
    org.wso2.store.modules.utils:2.1.0.SNAPSHOT
    org.wso2.store.modules.account-management:2.1.0.SNAPSHOT
    org.wso2.store.modules.login:2.1.0.SNAPSHOT
    org.wso2.store.modules.lifecycle:2.1.0.SNAPSHOT
    org.jaggeryjs.modules.markdown:1.0.0
    org.wso2.store.modules.event:2.1.0.SNAPSHOT
    org.wso2.carbon.core.ui:4.4.0
    org.jaggeryjs.modules.caramel:1.0.1
    org.wso2.store.modules.registration:2.1.0.SNAPSHOT
    org.wso2.store.modules.rxt:2.1.0.SNAPSHOT
