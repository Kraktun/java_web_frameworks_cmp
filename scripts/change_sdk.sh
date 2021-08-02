#! /bin/bash

# note: to execute this script  use command: . change_sdk.sh graal
# do not use: ./change_sdk.sh graal 
# because we must run it in the same env of the calling shell otherwise the changes to the sdk are lost

# all the other scripts must be run with ./ instead

source "$HOME/.sdkman/bin/sdkman-init.sh"

case $1 in
    graal)
        sdk use java 21.1.0.r11-grl
    ;;
    open)
        sdk use java 11.0.11-open
    ;;
    adopt-hs)
        sdk use java 11.0.11.hs-adpt
    ;;
    adopt-j9)
        sdk use java 11.0.11.j9-adpt
    ;;
    *)
        echo "Choose among: graal, open, adopt-hs, adopt-j9"
    ;;
esac 







