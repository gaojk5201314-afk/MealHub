var BASE_URL = 'https://5ebf74bf.r11.cpolar.top';
var CHAT_API = BASE_URL + '/api/chat/completions';

Page({
  data: {
    messages: [{ role: 'ai', content: '你好！我是 AI 助手，有什么可以帮助你的吗？' }],
    inputValue: '',
    loading: false,
    scrollToId: ''
  },
  goBack: function () {
    wx.navigateBack();
  },
  onInput: function (e) {
    this.setData({ inputValue: e.detail.value });
  },
  sendMessage: function () {
    var content = this.data.inputValue.trim();
    if (!content || this.data.loading) return;
    var userMsg = { role: 'user', content: content };
    this.setData({
      messages: this.data.messages.concat([userMsg]),
      inputValue: '',
      loading: true,
      scrollToId: 'msg-end'
    });
    this.callChatAPI(content);
  },
  callChatAPI: function (message) {
    var that = this;
    wx.request({
      url: CHAT_API,
      method: 'POST',
      header: { 'Content-Type': 'application/json' },
      dataType: 'text',
      data: { message: message },
      success: function (res) {
        console.log('Chat API success, status:', res.statusCode, 'data:', res.data);
        if (res.data && typeof res.data === 'string' && res.data.trim()) {
          that.setData({
            messages: that.data.messages.concat([{ role: 'ai', content: res.data }]),
            loading: false,
            scrollToId: 'msg-end'
          });
        } else if (res.data && typeof res.data === 'object' && res.data.message) {
          that.setData({
            messages: that.data.messages.concat([{ role: 'ai', content: res.data.message }]),
            loading: false,
            scrollToId: 'msg-end'
          });
        } else {
          var raw = typeof res.data === 'undefined' ? '无响应数据' : JSON.stringify(res.data);
          that.handleError('返回数据异常: ' + raw);
        }
      },
      fail: function (err) {
        console.error('Chat API fail:', err);
        that.handleError('网络请求失败，请检查后端服务是否启动 (https://5ebf74bf.r11.cpolar.top)');
      }
    });
  },
  handleError: function (errorMsg) {
    this.setData({
      messages: this.data.messages.concat([{ role: 'ai', content: '抱歉，' + errorMsg + '。请稍后再试。' }]),
      loading: false,
      scrollToId: 'msg-end'
    });
  }
});