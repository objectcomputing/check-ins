import React, { useCallback, useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import { useLocation, useHistory } from 'react-router-dom';
import queryString from 'query-string';
import ReviewPeriods from '../components/reviews/periods/ReviewPeriods';
import TeamReviews from '../components/reviews/TeamReviews';

const PREFIX = 'ReviewPage';
const classes = {
  root: `${PREFIX}-root`,
  headerContainer: `${PREFIX}-headerContainer`,
  requestHeader: `${PREFIX}-requestHeader`,
  stepContainer: `${PREFIX}-stepContainer`
};

const Root = styled('div')(({ theme }) => ({
  [`&.${classes.root}`]: {
    backgroundColor: 'transparent',
    margin: '4rem 2rem 2rem 2rem',
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

const ReviewPage = () => {
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

  const onPeriodSelected = useCallback(
    period => {
      setSelectedPeriod(period);
      handleQueryChange('period', period);
    },
    [handleQueryChange]
  );

  return (
    <Root className={classes.root}>
      <div className={classes.stepContainer}>
        {selectedPeriod === null ? (
          <ReviewPeriods onPeriodSelected={onPeriodSelected} />
        ) : (
          <TeamReviews
            onBack={() => setSelectedPeriod(null)}
            periodId={selectedPeriod}
          />
        )}
      </div>
    </Root>
  );
};

export default ReviewPage;
