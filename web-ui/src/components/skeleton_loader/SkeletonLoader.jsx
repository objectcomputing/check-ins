import React, { useEffect, useState } from 'react';
import { styled } from '@mui/material/styles';
import Skeleton from '@mui/material/Skeleton';
import { Card, CardHeader, CardContent, Grid, Box } from '@mui/material';
import { makeStyles } from '@mui/styles';

const PREFIX = 'SkeletonLoader';
const classes = {
  card: `${PREFIX}-card`
};

const StyledCard = styled(Card)(() => ({
  [`&.${classes.card}`]: {
    width: '340px'
  }
}));

const useStyles = makeStyles({
  noTopBottomPadding: {
    paddingBottom: 0,
    paddingTop: 0
  },
  smallMargin: {
    marginLeft: '1em'
  },
  flexShrink: {
    flexShrink: 1
  }
});

export default function SkeletonLoader({ type, delay = 300 }) {
  const additionalClasses = useStyles();
  
  const [showSkeleton, setShowSkeleton] = useState(false);

  // Introduce a delay before showing the skeleton loader
  useEffect(() => {
    const timer = setTimeout(() => {
      setShowSkeleton(true);
    }, delay);

    // Clear the timer on component unmount
    return () => clearTimeout(timer);
  }, [delay]);

  if (!showSkeleton) {
    return null; // Do not render the skeleton if the delay has not passed
  }

  if (type === 'team' || type === 'guild') {
    return (
      <StyledCard className={classes.card}>
        <CardHeader
          title={<Skeleton height={50} variant="text" />}
          subheader={<Skeleton variant="text" />}
        />
        <Box mt={2} ml={2} mr={2} height={80}>
          <Skeleton variant="text" height={15} />
          <Box height={10} />
          <Skeleton variant="text" height={15} />
          <Skeleton variant="text" width={180} height={15} />
        </Box>
      </StyledCard>
    );
  } else if (type === 'people') {
    return (
      <StyledCard className={classes.card}>
        <CardHeader
          title={<Skeleton height={43} variant="text" />}
          subheader={<Skeleton variant="text" />}
          avatar={<Skeleton variant="circular" width={45} height={45} />}
        />
        <Skeleton variant="rectangular" height={118} />
      </StyledCard>
    );
  } else if (type === 'feedback_requests') {
    return (
      <StyledCard width={100} style={{ marginTop: '1.7rem' }}>
        <CardContent className={additionalClasses.noTopBottomPadding}>
          <Grid container spacing={0}>
            <Grid item xs={12}>
              <Grid
                container
                direction="row"
                alignItems="center"
                justifyContent={'space-around'}
              >
                <Grid item>
                  <Skeleton variant="circular" width={40} height={40} />
                </Grid>
                <Grid item xs className={additionalClasses.flexShrink}>
                  <Skeleton
                    className={additionalClasses.smallMargin}
                    height={30}
                    variant="text"
                    width={'10vw'}
                  />
                  <Skeleton
                    className={additionalClasses.smallMargin}
                    height={30}
                    variant="text"
                    width={'15vw'}
                  />
                </Grid>
                <Grid item xs>
                  <Box
                    display="flex"
                    justifyContent="flex-end"
                    className={additionalClasses.flexShrink}
                  >
                    <Box
                      display="flex"
                      flexDirection="column"
                      alignItems="flex-end"
                    >
                      <Skeleton
                        height={30}
                        sx={{ m: 0 }}
                        variant="text"
                        width={'10vw'}
                      />
                      <Skeleton
                        height={30}
                        sx={{ m: 0 }}
                        variant="text"
                        width={'15vw'}
                      />
                      <Skeleton
                        height={'2vh'}
                        sx={{ m: 0 }}
                        variant="rectangular"
                        width={'2vh'}
                      />
                    </Box>
                  </Box>
                </Grid>
              </Grid>
            </Grid>
          </Grid>
        </CardContent>
      </StyledCard>
    );
  } else if (type === 'received_requests') {
    return (
      <Box
        display="flex"
        alignItems="center"
        justifyContent="center"
        width={'100%'}
      >
        <Skeleton
          variant="rectangular"
          height={'2.7rem'}
          width={'100%'}
        ></Skeleton>
      </Box>
    );
  } else if (type === 'view_feedback_responses') {
    return (
      <Box>
        <Skeleton
          className={additionalClasses.smallMargin}
          style={{ marginBottom: '1em' }}
          height={40}
          variant="text"
          width={'50vw'}
        />
        <StyledCard width={100}>
          <CardContent className={additionalClasses.noTopBottomPadding}>
            <Box
              display="flex"
              alignItems="center"
              justifyContent="center"
              width={'100%'}
            >
              <Skeleton
                variant="rectangular"
                height={'8vh'}
                width={'100%'}
              ></Skeleton>
            </Box>
          </CardContent>
        </StyledCard>
      </Box>
    );
  } else if (type === 'kudos') {
    return (
      <Box width={400}>
        <StyledCard padding={0}>
          <CardContent style={{ paddingBottom: 0 }}>
            <Box
              display="flex"
              flexDirection="column"
              width={"100%"}
              justifyContent="center"
            >
              <div style={{ display: "flex", flexDirection: "row", alignItems: "center", justifyContent: "space-between" }}>
                <Skeleton variant="h1" width={"30%"}/>
                <Skeleton variant="h1" width={"20%"}/>
              </div>
              <Skeleton
                variant="text"
                height={"6rem"}
                width={"100%"}
              />
            </Box>
          </CardContent>
        </StyledCard>
      </Box>
    );
  }

  return null;
}