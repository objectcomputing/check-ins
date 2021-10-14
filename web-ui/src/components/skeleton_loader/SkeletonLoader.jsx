import React from 'react'
import { styled } from '@mui/material/styles';
import Skeleton from '@mui/material/Skeleton';
import { Card, CardHeader, Box } from '@mui/material';

const PREFIX = 'SkeletonLoader';
const classes = {
  card: `${PREFIX}-card`
};

const StyledCard = styled(Card)(() => ({
  [`&.${classes.card}`]: {
    width: "340px",
  }
}));

export default function SkeletonLoader({type}) {
    // guild and team currently have the same return value but were given different conditionals
    // for clarity / in case one changes
    if (type === "team"){
        return (
        <StyledCard className={classes.card}>
          <CardHeader
              title={<Skeleton height={50} variant="text" />}
              subheader={<Skeleton variant="text" />}
          />
          <Box mt={2} ml={2} mr={2} height={80}>
            <Skeleton variant="text" height={15}/>
            <Box height={10}/>
            <Skeleton variant="text" height={15}/>
            <Skeleton variant="text" width={180} height={15}/>
          </Box>
        </StyledCard>
      );
    }
    else if (type === "guild"){
      return (
        <StyledCard className={classes.card}>
            <CardHeader
                title={<Skeleton height={50} variant="text" />}
                subheader={<Skeleton variant="text" />}
            />
            <Box mt={2} ml={2} mr={2} height={80}>
              <Skeleton variant="text" height={15}/>
              <Box height={10}/>
              <Skeleton variant="text" height={15}/>
              <Skeleton variant="text" width={180} height={15}/>
            </Box>
        </StyledCard>
      )
    }
    else if (type === "people"){
      return (
        <StyledCard className={classes.card}>
          <CardHeader
              title={<Skeleton height={43} variant="text" />}
              subheader={<Skeleton variant="text" />}
              avatar={<Skeleton variant="circular" width={45} height={45} />}
          />
          <Skeleton variant="rectangular"  height={118} />
        </StyledCard>
      );
    }
}
