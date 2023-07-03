import { Fragment } from 'react';
import ReactDOM from 'react-dom';

const Backdrop = (props) => {
  return (
    <div
      onClick={props.onClose}
      className=" fixed z-20 w-screen h-screen backdrop-blur-sm bg-black opacity-50"
    ></div>
  );
};

const ModalOverlay = (props) => {
  return (
    <div className="fixed z-30 top-50 top-1/2 left-1/2 transform -translate-x-1/2 -translate-y-1/2 border-2 mx-auto p-7 bg-white ">
      <div>{props.children}</div>
    </div>
  );
};

const portalElement = document.getElementById('overlays');

const Modal = (props) => {
  return (
    <>
      {ReactDOM.createPortal(
        <Backdrop onClose={props.onClose} />,
        portalElement
      )}
      {ReactDOM.createPortal(
        <ModalOverlay>{props.children}</ModalOverlay>,
        portalElement
      )}
    </>
  );
};

export default Modal;
