MyRecipe Android App

To run locally:
1. Install Firebase CLI: `npm install -g firebase-tools` (find further help for installation here: https://firebase.google.com/docs/cli#mac-linux-npm)
2. Configure the Functions Emulator and Firestore Emulator: `firebase init emulators` (find further help on the Local Emulator here: https://firebase.google.com/docs/emulator-suite/install_and_configure)
3. Start the Local Firebase Emulator: `firebase emulators:start`
4. In the MyRecipeApi.kt file of the Android app, change the value of `LOCAL_TESTING` to true
5. Run the Android App, it should now connect to the Cloud Functions and Firestore instance running on your local!
