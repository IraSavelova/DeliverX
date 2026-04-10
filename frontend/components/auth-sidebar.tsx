import { useState } from 'react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { MessageCircle, X, User, Mail, Lock } from 'lucide-react';

export function AuthSidebar() {
  const [isChatOpen, setIsChatOpen] = useState(false);
  const [isAuthOpen, setIsAuthOpen] = useState(false);
  const [chatMessages, setChatMessages] = useState<{ text: string; isUser: boolean }[]>([
    { text: 'Здравствуйте! Чем могу помочь?', isUser: false },
  ]);
  const [chatInput, setChatInput] = useState('');

  const handleSendMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!chatInput.trim()) return;

    setChatMessages([...chatMessages, { text: chatInput, isUser: true }]);
    setChatInput('');

    // Имитация ответа поддержки
    setTimeout(() => {
      setChatMessages((prev) => [
        ...prev,
        { text: 'Спасибо за ваш вопрос! Наш специалист свяжется с вами в ближайшее время.', isUser: false },
      ]);
    }, 1000);
  };

  const handleLogin = (e: React.FormEvent) => {
    e.preventDefault();
    alert('Функция входа будет доступна после подключения к базе данных');
  };

  const handleRegister = (e: React.FormEvent) => {
    e.preventDefault();
    alert('Функция регистрации будет доступна после подключения к базе данных');
  };

  return (
    <div className="fixed right-0 top-20 z-20 flex flex-col gap-3 mr-4">
      {/* Auth Button */}
      <Button
        onClick={() => {
          setIsAuthOpen(!isAuthOpen);
          setIsChatOpen(false);
        }}
        className="bg-gray-800 hover:bg-gray-700 rounded-full w-14 h-14 shadow-lg"
      >
        <User className="w-6 h-6" />
      </Button>

      {/* Chat Button */}
      <Button
        onClick={() => {
          setIsChatOpen(!isChatOpen);
          setIsAuthOpen(false);
        }}
        className="bg-orange-500 hover:bg-orange-600 rounded-full w-14 h-14 shadow-lg"
      >
        <MessageCircle className="w-6 h-6" />
      </Button>

      {/* Auth Panel */}
      {isAuthOpen && (
        <div className="absolute right-0 top-0 mt-32 w-96 bg-white rounded-xl shadow-2xl border border-gray-200">
          <div className="flex items-center justify-between p-4 border-b border-gray-200">
            <h3 className="font-semibold text-lg">Вход в систему</h3>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsAuthOpen(false)}
              className="h-8 w-8 p-0"
            >
              <X className="w-4 h-4" />
            </Button>
          </div>

          <div className="p-4">
            <Tabs defaultValue="login">
              <TabsList className="grid w-full grid-cols-2">
                <TabsTrigger value="login">Вход</TabsTrigger>
                <TabsTrigger value="register">Регистрация</TabsTrigger>
              </TabsList>

              <TabsContent value="login" className="space-y-4">
                <form onSubmit={handleLogin} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="login-email">Email</Label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <Input
                        id="login-email"
                        type="email"
                        placeholder="example@mail.com"
                        className="pl-10"
                        required
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="login-password">Пароль</Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <Input
                        id="login-password"
                        type="password"
                        placeholder="••••••••"
                        className="pl-10"
                        required
                      />
                    </div>
                  </div>

                  <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">
                    Войти
                  </Button>

                  <a href="#" className="text-sm text-blue-600 hover:underline block text-center">
                    Забыли пароль?
                  </a>
                </form>
              </TabsContent>

              <TabsContent value="register" className="space-y-4">
                <form onSubmit={handleRegister} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="register-name">Имя</Label>
                    <div className="relative">
                      <User className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <Input
                        id="register-name"
                        type="text"
                        placeholder="Ваше имя"
                        className="pl-10"
                        required
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="register-email">Email</Label>
                    <div className="relative">
                      <Mail className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <Input
                        id="register-email"
                        type="email"
                        placeholder="example@mail.com"
                        className="pl-10"
                        required
                      />
                    </div>
                  </div>

                  <div className="space-y-2">
                    <Label htmlFor="register-password">Пароль</Label>
                    <div className="relative">
                      <Lock className="absolute left-3 top-3 w-4 h-4 text-gray-400" />
                      <Input
                        id="register-password"
                        type="password"
                        placeholder="••••••••"
                        className="pl-10"
                        required
                      />
                    </div>
                  </div>

                  <Button type="submit" className="w-full bg-blue-600 hover:bg-blue-700">
                    Зарегистрироваться
                  </Button>
                </form>
              </TabsContent>
            </Tabs>
          </div>
        </div>
      )}

      {/* Chat Panel */}
      {isChatOpen && (
        <div className="absolute right-0 top-0 mt-32 w-96 bg-white rounded-xl shadow-2xl border border-gray-200 flex flex-col h-[500px]">
          <div className="flex items-center justify-between p-4 border-b border-gray-200 bg-orange-500 rounded-t-xl">
            <h3 className="font-semibold text-lg text-white">Чат поддержки</h3>
            <Button
              variant="ghost"
              size="sm"
              onClick={() => setIsChatOpen(false)}
              className="h-8 w-8 p-0 text-white hover:bg-orange-600"
            >
              <X className="w-4 h-4" />
            </Button>
          </div>

          <div className="flex-1 overflow-y-auto p-4 space-y-3">
            {chatMessages.map((message, index) => (
              <div
                key={index}
                className={`flex ${message.isUser ? 'justify-end' : 'justify-start'}`}
              >
                <div
                  className={`max-w-[80%] rounded-lg p-3 ${
                    message.isUser
                      ? 'bg-orange-500 text-white'
                      : 'bg-gray-100 text-gray-800'
                  }`}
                >
                  {message.text}
                </div>
              </div>
            ))}
          </div>

          <form onSubmit={handleSendMessage} className="p-4 border-t border-gray-200">
            <div className="flex gap-2">
              <Input
                value={chatInput}
                onChange={(e) => setChatInput(e.target.value)}
                placeholder="Введите сообщение..."
                className="flex-1"
              />
              <Button type="submit" className="bg-orange-500 hover:bg-orange-600">
                Отправить
              </Button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
}