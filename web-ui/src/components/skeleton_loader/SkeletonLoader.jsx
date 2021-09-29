import React from 'react'
import Skeleton from '@mui/material/Skeleton';

import makeStyles from '@mui/styles/makeStyles';
import { Card, CardHeader, Box } from '@mui/material';

const useStyles = makeStyles((theme) => ({
  card: {
    width: "340px",
  }

}));


export default function SkeletonLoader({type}) {
    const classes = useStyles();
    // guild and team currently have the same return value but were given different conditionals
    // for clarity / in case one changes
    if (type === "team"){
        return (
        <Card className={classes.card}>
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
       </Card>
      )
    }
    else if (type === "guild"){
      return (
        <Card className={classes.card}>
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

        </Card>
      )
    }
    else if (type === "people"){
      return (
        <Card className={classes.card}>
          <CardHeader 
              title={<Skeleton height={43} variant="text" />}
              subheader={<Skeleton variant="text" />}
              avatar={<Skeleton variant="circular" width={45} height={45} />}
          />
          <Skeleton variant="rectangular"  height={118} />
        </Card>
      );
    }
}
