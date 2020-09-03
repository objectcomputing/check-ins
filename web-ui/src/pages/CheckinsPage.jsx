import React, {useState} from 'react';
import CheckinsHistory from '../components/checkin/CheckinHistory';
import CheckinDocs from '../components/checkin/CheckinDocs';
import Personnel from '../components/personnel/Personnel';
import Modal from '../components/modal/Modal';
import Button from '@material-ui/core/Button';
import GuidesPanel from '../components/guides/GuidesPanel';
import ActionItemsPanel from '../components/action_item/ActionItemsPanel';

import './CheckinsPage.css';

const CheckinsPage = () => {
  const [show, setShow] = useState(false);

  const showModal = () => {
    setShow(!show);
  };
  return (
    <div>
      <div className="container">
        <div className="contents">
          <CheckinsHistory />
        </div>
        <div className="right-sidebar">
          <Personnel />
          <GuidesPanel />
        </div>
      </div>
      <CheckinDocs />
      <div className="modal-container">
        <ActionItemsPanel checkinId="c6e33f72-fce6-43aa-996a-51a15864acf2" />
        <Modal close={showModal} show={show}>
          The checkin will no longer be able to be edited. Are you sure that you
          are ready to close this check-in?
        </Modal>
        <Button
          style={{
            backgroundColor: '#3f51b5',
            color: 'white',
            display: show ? 'none' : ''
          }}
          onClick={() => showModal()}
        >
          Submit
        </Button>
      </div>
    </div>
  );
};

export default CheckinsPage;
