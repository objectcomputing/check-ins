import { applyMiddleware, createStore, compose } from 'redux';
import thunk from 'redux-thunk';
import { persistStore, persistReducer } from 'redux-persist';
import storage from 'redux-persist/lib/storage'; // defaults to localStorage for web
import rootReducer from '../reducers/rootReducer';

const persistConfig = {
  key: 'root',
  storage,
  whitelist: ['login', 'profile', 'educationReducer'] // add more reducers here for whitelisting as they're created
  // blacklist: ['']
};

// Configure the Redux store
const configureStore = () => {
  const middlewares = [thunk];

  // Enable Redux Dev Tools if installed
  const composeEnhancers =
    window.__REDUX_DEVTOOLS_EXTENSION_COMPOSE__ || compose;

  // Apply the middleware and enhancers
  const enhancers = composeEnhancers(applyMiddleware(...middlewares));

  const persistedReducer = persistReducer(persistConfig, rootReducer());

  // Create the store
  let store = createStore(persistedReducer, enhancers);

  // Create the persistor
  let persistor = persistStore(store);
  // console.log(store.getState());
  return { store, persistor };
};

export default configureStore;
