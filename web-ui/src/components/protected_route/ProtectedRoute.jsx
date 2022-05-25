import React, {useContext, useEffect, useState} from "react";
import { Route } from "react-router-dom";
import PropTypes from "prop-types";
import {AppContext} from "../../context/AppContext";
import {userHasPermissionForRoute} from "../../context/routePermissions";

const propTypes = {
  path: PropTypes.string.isRequired,
  exact: PropTypes.bool
};

const ProtectedRoute = (props) => {
  const { state } = useContext(AppContext);
  const { userPermissions } = state;

  const [hasPermission, setHasPermission] = useState(true);

  useEffect(() => {
    setHasPermission(userHasPermissionForRoute(props.path, userPermissions));
  }, [props, userPermissions]);

  return (
    hasPermission && (
      <Route exact={!!props.exact} path={props.path}>
        {props.children}
      </Route>
    )
  );
}

ProtectedRoute.propTypes = propTypes;

export default ProtectedRoute;