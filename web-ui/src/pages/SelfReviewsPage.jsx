import React, { useContext, useCallback, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import Typography from '@mui/material/Typography';
import { useLocation, useHistory } from 'react-router-dom';
import queryString from 'query-string';
import { AppContext } from '../context/AppContext';
import { selectCurrentUser } from '../context/selectors';
import ReviewPeriods from '../components/reviews/periods/ReviewPeriods';
import SelfReview from '../components/reviews/SelfReview';

const PREFIX = 'SelfReviewPage';
const classes = {
  root: `${PREFIX}-root`,
  headerContainer: `${PREFIX}-headerContainer`,
  requestHeader: `${PREFIX}-requestHeader`,
  stepContainer: `${PREFIX}-stepContainer`
};

const Root = styled('div')(({ theme }) => ({
  [`&.${classes.root}`]: {
    backgroundColor: 'transparent',
    padding: '4rem 2rem 0 2rem',
    height: '100%',
    maxWidth: '100%',
    ['@media (max-width:800px)']: {
      // eslint-disable-line no-useless-computed-key
      display: 'flex',
      flexDirection: 'column',
      overflowX: 'hidden',
      margin: '2rem 5% 0 5%'
    }
  },
  [`& .${classes.headerContainer}`]: {
    display: 'flex',
    flexDirection: 'row',
    justifyContent: 'space-between',
    alignItems: 'center',
    margin: '0 0 2em 0',
    ['@media (max-width:800px)']: {
      // eslint-disable-line no-useless-computed-key
      margin: '0',
      justifyContent: 'center'
    }
  },
  [`& .${classes.requestHeader}`]: {
    ['@media (max-width:800px)']: {
      // eslint-disable-line no-useless-computed-key
      fontSize: 'x-large',
      marginBottom: '1em'
    }
  },
  [`& .${classes.stepContainer}`]: {
    ['@media (max-width:800px)']: {
      // eslint-disable-line no-useless-computed-key
      maxWidth: '100%'
    },
    backgroundColor: 'transparent'
  }
}));

const SelfReviewPage = () => {
  const { state } = useContext(AppContext);
  const memberProfile = selectCurrentUser(state);
  const location = useLocation();
  const history = useHistory();
  const [query, setQuery] = useState([]);
  const [selectedPeriod, setSelectedPeriod] = useState(null);

  const handleQueryChange = useCallback(
    (key, value) => {
      let newQuery = {
        ...query,
        [key]: value
      };
      history.push({ ...location, search: queryString.stringify(newQuery) });
    },
    [history, location, query]
  );

  const hasPeriod = useCallback(() => {
    return !!query.period;
  }, [query.period]);

  const getPeriod = useCallback(() => {
    if (hasPeriod()) return query.period;
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
    handleQueryChange('period', undefined);
  }, [handleQueryChange]);

  const onPeriodSelected = useCallback(
    period => {
      handleQueryChange('period', period);
    },
    [handleQueryChange]
  );

  return (
    <Root className={classes.root}>
      {!selectedPeriod && (
        <div className={classes.headerContainer}>
          <Typography className={classes.requestHeader} variant="h4">
            Self-Reviews for{' '}
            <b>{memberProfile?.firstName + ' ' + memberProfile?.lastName}</b>
          </Typography>
        </div>
      )}
      <div className={classes.stepContainer}>
        {selectedPeriod === null ? (
          <ReviewPeriods onPeriodSelected={onPeriodSelected} mode="self" />
        ) : (
          <SelfReview periodId={selectedPeriod} onBack={clearPeriod} />
        )}
      </div>
    </Root>
  );
};

export default SelfReviewPage;
