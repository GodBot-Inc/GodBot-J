echo "About to copy Executables"

rm /Volumes/GodBotShare/.env
cp .env /Volumes/GodBotShare/.env
rm /Volumes/GodBotShare/GodBot.jar
cp ./out/artifacts/GodBot_jar/GodBot.jar /Volumes/GodBotShare/GodBot.jar
cp refresh.txt /Volumes/GodBotShare/refresh.txt

echo "Moving Successful"
