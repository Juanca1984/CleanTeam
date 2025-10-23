import { useNavigate } from 'react-router-dom';
import Button from '../components/Button';

const HomePage = () => {
  const navigate = useNavigate();

  return (
    <div
      style={{
        display: 'flex',
        flexDirection: 'column',
        gap: '20px',
        alignItems: 'center',
        justifyContent: 'center',
        width: '100%',
      }}
    >
      <h1 style={{ color: 'var(--secondary-color)', fontSize: '3rem' }}>
        Mandarin Player
      </h1>
      <Button onClick={() => navigate('/login')} variant="primary">
        Log In
      </Button>
      <Button onClick={() => navigate('/signup')} variant="secondary">
        Sign Up
      </Button>
      <Button onClick={() => navigate('/join')} variant="primary">
        Join Game
      </Button>
    </div>
  );
};

export default HomePage;