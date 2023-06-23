const functions = require("firebase-functions");
const https = require("https");

// The Firebase Admin SDK to access Firestore.
const admin = require("firebase-admin");
admin.initializeApp();

const API_KEY = "cfd7ce778abe46ebaa9a31911ef740d5";
const API_KEY_URL = `&apiKey=${API_KEY}`;

exports.helloWorld = functions.https.onRequest((request, response) => {
  functions.logger.info("Hello logs!", {structuredData: true});
  response.send({res: "Hello from Firebase!"});
});

exports.getRandomRecipes = functions.https.onRequest((req, gcpRes) => {
  const number = req.query.number;
  const filters = req.query.filters;
  const dairyFree = req.query.dairyFree;
  const glutenFree = req.query.glutenFree;
  functions.logger
      .info(`GetRandomRecipes Request: ${JSON.stringify(req.query)}`);

  let url = "https://api.spoonacular.com/recipes/random?";
  if (number != null && number != undefined) url += `&number=${number}`;
  if (filters != null && filters != undefined) url += `&tags=${filters}`;
  url += API_KEY_URL;

  getRecipe(url, dairyFree, glutenFree, gcpRes);
});

// eslint-disable-next-line require-jsdoc
function getRecipe(url, dairyFree, glutenFree, gcpRes) {
  httpRequest(url,
      (response) => {
        let recipeData = response.recipes;
        if (dairyFree) {
          recipeData = recipeData.filter(
              (recipe) =>
                (recipe.dairyFree),
          );
        }
        if (glutenFree) {
          recipeData = recipeData.filter(
              (recipe) =>
                (recipe.glutenFree),
          );
        }
        if (recipeData.length == 0) {
          getRecipe(url, dairyFree, glutenFree, gcpRes);
        } else {
          const recipes = recipeData.map(
              (recipe) =>
                ({id: recipe.id, image: recipe.image, title: recipe.title}),
          );
          gcpRes.status(200).send(recipes);
        }
      },
      (err) => gcpRes.status(500).send(`Error: ${err.message}`));
}

exports.getRecipeInstructions = functions.https.onRequest((req, gcpRes) => {
  const id = req.query.id;
  functions.logger
      .info(`GetRecipes Request: ${JSON.stringify(req.query)}`);

  let url = `https://api.spoonacular.com/recipes/${id}/analyzedInstructions?stepBreakdown=false`;
  url += API_KEY_URL;

  httpRequest(url,
      (response) => {
        let instructions = [];
        if (response && response[0] && response[0].steps) {
          instructions = response[0].steps;
        }
        const steps = [];
        const ingredients = new Set();
        const equipment = new Set();
        instructions.forEach(
            (step) => {
              steps.push({number: step.number, instruction: step.step});
              if (step.ingredients && step.ingredients.length > 0) {
                step.ingredients.forEach(
                    (ingredient) => ingredients.add(ingredient.name),
                );
              }
              if (step.equipment && step.equipment.length > 0) {
                step.equipment.forEach(
                    (tool) => equipment.add(tool.name),
                );
              }
            },
        );

        const recipeInstructions = {
          id: response.id,
          equipment: Array.from(equipment),
          ingredients: Array.from(ingredients),
          steps: steps,
        };

        gcpRes.status(200).send(recipeInstructions);
      },
      (err) => gcpRes.status(500).send(`Error: ${err.message}`));
});

exports.getUserRecipes = functions.https.onRequest(async (req, gcpRes) => {
  const username = req.query.username;
  functions.logger
      .info(`SaveRecipe Request: ${JSON.stringify(req.query)}`);

  const recipeData = await admin.firestore()
      .collection("recipes")
      .doc(username)
      .collection("saved")
      .get();

  const recipes = recipeData.docs.map(
      (recipe) =>
        ({
          id: recipe.id,
          image: recipe.data().url,
          title: recipe.data().name,
          cooked: recipe.data().cooked,
          favorite: recipe.data().favorite,
          dateAdded: recipe.data().dateAdded,
        }),
  );

  gcpRes.status(200).send(recipes);
});

exports.saveRecipe = functions.https.onRequest(async (req, gcpRes) => {
  const body = req.body;
  functions.logger
      .info(`SaveRecipe Request: ${JSON.stringify(body)}`);

  const writeResult = await admin.firestore()
      .collection("recipes")
      .doc(body.username)
      .collection("saved")
      .doc(body.id)
      .set(body.recipe);

  gcpRes.json({result: `Message with ID: ${writeResult.id} updated.`});
});

/** @function
 * @param {string} url
 * @param {function} onSuccess
 * @param {function} onFailure
*/
function httpRequest(url, onSuccess, onFailure) {
  https.get(url, (res) => {
    const data = [];
    functions.logger
        .info(`Status Code: ${res.statusCode}`);

    res.on("data", (chunk) => {
      data.push(chunk);
    });

    res.on("end", () => {
      const response = JSON.parse(Buffer.concat(data).toString());
      onSuccess(response);
    });
  }).on("error", (err) => {
    functions.logger
        .info(`Error Message: ${err.message}`, {structuredData: true});
    onFailure(err);
  });
}
