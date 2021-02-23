import React, {useContext, useEffect} from 'react';
import {AppContext, AppContextProvider, UPDATE_CHECKINS} from '../../context/AppContext';
import CheckinHistory from './CheckinHistory';
import { createMemoryHistory } from "history";
import { Router } from "react-router-dom";

const history = createMemoryHistory('/checkins');

export default {
    title: 'Check-Ins/CheckinHistory',
    component: CheckinHistory,
    decorators: [(Story) => {
        return (<Router history={history}><AppContextProvider><Story/></AppContextProvider></Router>);
    }]
};
const checkins = [	{
		"id": "bbc3db2a-181d-4ddb-a2e4-7a9842cdfd78",
		"teamMemberId": "43ee8e79-b33d-44cd-b23c-e183894ebfef",
		"pdlId": "2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d",
		"checkInDate": [
			2020,
			9,
			29,
			11,
			32,
			29,
			40000000
		],
		"completed": true
    }, 
    {
		"id": "bbc3db2a-181d-4ddb-a2e4-7a9842cdfd78",
		"teamMemberId": "43ee8e79-b33d-44cd-b23c-e183894ebfef",
		"pdlId": "2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d",
		"checkInDate": [
			2020,
			10,
			29,
			11,
			32,
			29,
			40000000
		],
		"completed": true
    }, 
    {
		"id": "bbc3db2a-181d-4ddb-a2e4-7a9842cdfd78",
		"teamMemberId": "43ee8e79-b33d-44cd-b23c-e183894ebfef",
		"pdlId": "2c1b77e2-e2fc-46d1-92f2-beabbd28ee3d",
		"checkInDate": [
			2020,
			11,
			29,
			11,
			32,
			29,
			40000000
		],
		"completed": false
	}
];

const onlyOneCheckIn = [checkins[2]]

const SetCheckins = ({checkins}) => {
    const { dispatch } = useContext(AppContext);
    useEffect(() => {
        dispatch({ type: UPDATE_CHECKINS, payload: checkins });
    }, [checkins, dispatch]);
    return "";
}

const Template = (args) => (
    <React.Fragment>
        <SetCheckins checkins={args.checkins} />
        <CheckinHistory {...args} />
    </React.Fragment>
);

export const MultipleCheckIns = Template.bind({});
MultipleCheckIns.args = {
    history,
    checkins
};

export const SingleCheckIn = Template.bind({});
SingleCheckIn.args = {
    history,
    checkins: onlyOneCheckIn
};
