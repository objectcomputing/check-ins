import React, {useContext, useCallback, useEffect, useState} from "react";
import { styled } from '@mui/material/styles';
import Typography from "@mui/material/Typography";
import { useLocation, useHistory } from 'react-router-dom';
import queryString from 'query-string';
import {AppContext} from "../context/AppContext";
import {selectCurrentUser} from "../context/selectors";
import ReviewPeriods from "../components/reviews/periods/ReviewPeriods";

const PREFIX = 'ReviewPage';
const classes = {
  root: `${PREFIX}-root`,
  headerContainer: `${PREFIX}-headerContainer`,
  requestHeader: `${PREFIX}-requestHeader`,
  stepContainer: `${PREFIX}-stepContainer`,
};

const Root = styled('div')(({theme}) => ({
  [`&.${classes.root}`]: {
    backgroundColor: "transparent",
    margin: "4rem 2rem 2rem 2rem",
    height: "100%",
    'max-width': "100%",
    ['@media (max-width:800px)']: { // eslint-disable-line no-useless-computed-key
      display: "flex",
      'flex-direction': "column",
      'overflow-x': "hidden",
      margin: "2rem 5% 0 5%",
    }
  },
  [`& .${classes.headerContainer}`]: {
    display: "flex",
    'flex-direction': "row",
    'justify-content': "space-between",
    'align-items': "center",
    margin: "0 2em 20px 2em",
    ['@media (max-width:800px)']: { // eslint-disable-line no-useless-computed-key
      margin: "0",
      'justify-content': "center",
    }
  },
  [`& .${classes.requestHeader}`]: {
    ['@media (max-width:800px)']: { // eslint-disable-line no-useless-computed-key
      fontSize: "x-large",
      marginBottom: "1em",
    },
  },
  [`& .${classes.stepContainer}`]: {
    ['@media (max-width:800px)']: { // eslint-disable-line no-useless-computed-key
      'max-width': "100%",
    },
    backgroundColor: "transparent"
  },
}));

const ReviewPage = () => {
  const {state} = useContext(AppContext);
  const memberProfile = selectCurrentUser(state);
  const location = useLocation();
  const history = useHistory();
  const [query, setQuery] = useState([]);
  const [selectedPeriod, setSelectedPeriod] = useState(null);

  const handleQueryChange = useCallback((key, value) => {
    let newQuery = {
      ...query,
      [key]: value
    }
    history.push({ ...location, search: queryString.stringify(newQuery) });
  }, [history, location, query]);

  const hasPeriod = useCallback(() => {
    return !!query.period;
  }, [query.period]);

  const getPeriod = useCallback(() => {
    if (hasPeriod())
      return query.period;
    else return null;
  }, [query.period, hasPeriod]);

  useEffect(() => {
    setSelectedPeriod(getPeriod());
  }, [query.period, getPeriod]);

  useEffect(() => {
    const params = queryString.parse(location?.search);
    setQuery(params);
  }, [location.search]);

  const clearPeriod = useCallback(() => {
    handleQueryChange("period", undefined);
  }, [handleQueryChange]);

  const onPeriodSelected = useCallback((period) => {
      handleQueryChange("period", period);
    }, [handleQueryChange]);

  return (
    <Root className={classes.root}>
      <div className={classes.headerContainer}>
        <Typography className={classes.requestHeader} variant="h4">Team Reviews<b>{memberProfile?.name}</b></Typography>
      </div>
      <div className={classes.stepContainer}>
        {
            selectedPeriod === null ?
                (<ReviewPeriods onPeriodSelected={onPeriodSelected} />) :
                (<Typography variant="h5" onClick={clearPeriod}>{selectedPeriod}</Typography>)
        }
      </div>
    </Root>
  );
}

export default ReviewPage;
