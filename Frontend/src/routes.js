import React from 'react';
import { Router, Route, browserHistory } from 'react-router'

import App from './app/App';

export const routes = (
    <Router history={browserHistory}>
        <Route path="/" component={App}>
        </Route>
    </Router>
);