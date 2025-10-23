import { Link, useNavigate } from 'react-router-dom';
import Button from '../components/Button';
import Input from '../components/Input';

const SignUpPage = () => {
  const navigate = useNavigate();

  return (
    <div style={{ maxWidth: '400px', margin: 'auto' }}>
      <h2 style={{ color: 'var(--secondary-color)' }}>Sign Up</h2>
      <form>
        <Input type="text" placeholder="Username" required />
        <Input type="email" placeholder="Email" required />
        <Input type="password" placeholder="Password" required />
        <div style={{ display: 'flex', gap: '10px', marginTop: '20px' }}>
          <Button type="submit" style={{ flex: 1 }}>
            Create Account
          </Button>
          <Button type="button" onClick={() => navigate('/')} variant="secondary" style={{ flex: 1 }}>
            Return
          </Button>
        </div>
      </form>
      <p>
        Already have an account?{' '}
        <Link to="/login" style={{ color: 'var(--secondary-color)' }}>
          Log In
        </Link>
      </p>
    </div>
  );
};

export default SignUpPage;