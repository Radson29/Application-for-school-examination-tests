import Modal from 'components/UI/Modal';
import { useForm } from 'react-hook-form';
import Swal from 'sweetalert2';
import { useNavigate } from 'react-router-dom';

import { useMutation } from 'react-query';
import axios from 'api/axios';

const EditQuizModal = (props) => {
  return <Modal onClose={props.onClose}></Modal>;
};

export default EditQuizModal;
