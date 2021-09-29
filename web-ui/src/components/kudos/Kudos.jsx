import { Box, Card, CardContent, CardHeader, Typography } from '@mui/material';
import makeStyles from '@mui/styles/makeStyles';
import React from 'react'
import AvatarComponent from '../avatar/Avatar'

const useStyles = makeStyles((theme) => ({
  display: {
    display: "flex",
    flexDirection: "column",
    justifyContent: "space-around"
  },
  small: {
    width: theme.spacing(6),
    height: theme.spacing(6),
    margin: theme.spacing(1),
  },
  large: {
    width: theme.spacing(8),
    height: theme.spacing(8),
  },
}));

export default function Kudos({kudosTo, kudosFrom, content}) {
  const classes = useStyles();
  return (
    <Card classes={{root: classes.display}}>
      <Box display="flex" justifyContent="center">
        <CardHeader
          avatar={
            <AvatarComponent className={classes.large} imageUrl={kudosTo.imageUrl} />
          }
          disableTypography
          title={
            <Typography variant="h5" component="h2">
              {`Kudos to ${kudosTo.name} 🎉`}
            </Typography>
          }
        />
      </Box>
      <CardContent>
      <Typography>
        <Box component={'span'} textAlign="center">
            {content}
        </Box>
      </Typography>
      {kudosFrom && 
        <Box display="flex" justifyContent="flex-end" alignItems="center">
          <Typography variant="subtitle2">
              {`${kudosFrom.name} - ${kudosFrom.title}`}
            </Typography>
          <AvatarComponent className={classes.small} imageUrl={kudosFrom.imageUrl} />
        </Box>
      }
      </CardContent>
    </Card>
  )
}


