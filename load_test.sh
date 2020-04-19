./gradlew testLoad -Pgatling-date-time-base-url="http://date-time-tf-477828465.us-east-2.elb.amazonaws.com"
echo ""
echo ""
echo "#####################################################"
echo "If running from driver linux machine view results at:"
REPORT_FOLDER=`ls -t build/gatling-results | head -1`
echo "http://18.220.118.143/html/gatling/${REPORT_FOLDER}"
echo "#####################################################"
