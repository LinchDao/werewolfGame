# werewolfGame
### 在游玩线下经典版狼人杀时可作为游戏法官助手

#### 项目启动后访问ip:8080/entry进入入口界面
1. 重置游戏配置：设置玩家数量、狼人数量、猎人数量、游戏规则。默认是8，3，1  
2. 语音设置：设置游戏法官声音
---
在家中聚会和朋友玩狼人杀的时候，需要一个人来主持当法官。  
这么玩就要求法官必须要对游戏的流程比较清晰，一旦有失误很可能就会导致游戏不得不重新开始。  
但如果法官对游戏流程清晰，说明他有兴趣并且经常玩这个游戏，对于比较享受游戏对局的法官来说可能体验较差（当然玩的时候肯定是轮流来当）  
并且玩这个游戏，由人来当法官可能会出现以下问题：  
- 虽然别人都闭着眼，但是狼人在刚睁眼的时候大多是鬼鬼祟祟的，这就导致了他们在确认身份时会忍不住笑出来从而暴露身份。
- 法官在主持游戏的过程中也可能会意外暴露玩家身份，比如说话的声音明显朝向某人
- 法官可能会错误的判断游戏结束，从而使得尽力游戏的玩家非常难受（明明就差一点！！hhh）
- 夜晚睁眼的玩家可能会看到闭眼玩家的表情之类的想笑而暴露身份。
- 游戏中出现某些情况意外在夜间睁眼
---
反正我们玩的时候经常因为忍不住笑而暴露  
我认为开发的这个程序可以很大程度上解决这个问题  
我预想的游戏流程是这样的
1. 游戏参与者和电脑处于同一个网络，电脑启动程序，所有玩家用浏览器访问电脑ip:8080/entry或/join
2. 游戏设置、语音设置（也算是在刚开始的时候增加一定的趣味性吧）
2. 所有人给自己命名后加入房间
3. 开始游戏（在各个阶段程序会播放语音来提醒玩家该做什么）
4. 在闭眼时，玩家可以真正的闭眼或者直接趴在桌子上看着手机就行（总之不要让别人看到你在操作游戏界面避免暴露身份），狼人、预言家、女巫可以直接操作手机来进行角色操作或者确认同伴。  
5. 在睁眼时，玩家沟通之后，通过手机投票来处决玩家。  

这样玩由于保留了闭眼的操作和语音播报让玩家确认身份等环节，可以最大程度的控制游戏节奏（节奏太快不好玩）  
也不会减少太多的趣味性，以上由玩家担任法官的问题何尝不是种趣味。 
其实核心目的是大家都能参与进来，这样只有程序才知道所有人的身份，
也增加了探索未知的快乐（我们有老六经常通过观察法官或者别人的表情来确认一个人的身份）。   
***
总之享受游戏吧。