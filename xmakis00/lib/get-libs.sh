FILE="javafx.zip"
FOLDER="javafx-sdk-11.0.2"

if [[ "$OSTYPE" == "linux-gnu"* ]]; then
  # Linux environment
  DOWNLOAD="https://gluonhq.com/download/javafx-11-0-2-sdk-linux/"
elif [[ "$OSTYPE" == "darwin"* ]]; then
  # Mac environment
  DOWNLOAD="https://gluonhq.com/download/javafx-11-0-2-sdk-mac"
else
  # Other environment
  DOWNLOAD="https://gluonhq.com/download/javafx-11-0-2-sdk-linux/"
fi

PARSED_PATH=$(echo "$0" | sed 's/get-libs.sh$//')
PARSED_PATH+="."

wget -O "$PARSED_PATH/$FILE" $DOWNLOAD
unzip "$PARSED_PATH/$FILE" -d $PARSED_PATH

mv $PARSED_PATH/$FOLDER/lib/* $PARSED_PATH/

rm -rf "$PARSED_PATH/$FOLDER"
rm -f "$PARSED_PATH/$FILE"
