import axios from 'axios';

async function testLogin() {
  try {
    const response = await axios.post('http://localhost:8082/api/users/login/phone', {
      phone: '13800138000'
    });
    console.log('Login response:', response.data);
  } catch (error) {
    console.error('Login error:', error.message);
    if (error.response) {
      console.error('Response data:', error.response.data);
      console.error('Response status:', error.response.status);
    }
  }
}

testLogin();