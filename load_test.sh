./gradlew testLoad -Pgatling-date-time-base-url="http://date-time-tf-477828465.us-east-2.elb.amazonaws.com"
MY_IP=`dig +short myip.opendns.com @resolver1.opendns.com`
REPORT_FOLDER=`ls -t build/gatling-results | head -1`
echo ""
echo ""
echo "#####################################################"
echo "If running from driver linux machine view results at:"
echo "http://${MY_IP}/html/gatling/${REPORT_FOLDER}"
echo "#####################################################"