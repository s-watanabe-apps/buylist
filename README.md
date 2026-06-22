# お買い物リスト

シンプルで直感的なAndroidショッピングリストアプリケーション。あなたの買い物を効率的に管理するのに役立ちます。

## 概要

お買い物リストは、複数のショッピングリストを作成し、商品を価格とメモ付きで追加でき、合計金額を自動計算できるAndroidアプリです。ライトモードとダークモードの両方に対応し、ショッピングニーズを管理するためのクリーンなインターフェースを提供します。

## 機能

- **複数のショッピングリスト**: さまざまな用途に合わせて複数のショッピングリストを作成・管理
- **商品管理**: 以下の情報を含む商品の追加・編集・削除が可能：
  - 商品名
  - 金額
  - フリーメモ
- **自動合計計算**: ショッピングリストの合計金額を自動計算
- **テーマサポート**:
  - 端末のデフォルト設定
  - ライトモード
  - ダークモード
- **柔軟なアイテム順序**:
  - リストの先頭または最後に追加
  - 商品の先頭または最後に追加
- **設定**: アプリの動作をカスタマイズ
- **多言語対応**: 英語と日本語に対応
- **Google AdMob統合**: 広告をサポート

## 技術仕様

### プロジェクト構成

```
buylist/
├── app/                          # Androidアプリモジュール
│   ├── src/
│   │   ├── main/                # メインソースコード
│   │   │   ├── java/com/swapps/buylist/
│   │   │   ├── res/             # リソース（レイアウト、文字列、ドローアブル）
│   │   │   └── AndroidManifest.xml
│   │   ├── test/                # ユニットテスト
│   │   └── androidTest/         # インストルメント化テスト
│   ├── build.gradle             # アプリビルド設定
│   └── release/                 # リリースAPK/AABファイル
├── gradle/                       # Gradle設定
├── build.gradle                 # ルートビルド設定
├── settings.gradle              # Gradle設定ファイル
└── gradle.properties            # Gradleプロパティ
```

### 必要な環境

- **最小SDK**: Android 7.0（APIレベル24）
- **ターゲットSDK**: Android 15（APIレベル36）
- **言語**: Java/Kotlin
- **Javaバージョン**: Java 11

### 依存ライブラリ

- AndroidX AppCompat
- AndroidX Activity
- AndroidX ConstraintLayout
- Material Design コンポーネント
- Jackson Databind（JSON処理）
- Google Play Services Ads
- JUnit（テスト）
- Espresso（UIテスト）

### ビルド情報

- **現在のバージョン**: 1.2
- **バージョンコード**: 3
- **パッケージ名**: `com.swapps.buylist`
- **ビルドツール**: Gradle with Android Gradle Plugin

## はじめに

### 前提条件

- Android Studio（最新版を推奨）
- Gradle 8.0以上
- Java 11以上

### プロジェクトのビルド

1. リポジトリをクローン
   ```bash
   git clone <repository-url>
   cd buylist
   ```

2. Gradleでビルド
   ```bash
   ./gradlew build
   ```

3. リリースAPKをビルド
   ```bash
   ./gradlew assembleRelease
   ```

4. リリースAAB（Android App Bundle）をビルド
   ```bash
   ./gradlew bundleRelease
   ```

### アプリの実行

1. Android Studioでプロジェクトを開く
2. Androidデバイスを接続するか、エミュレーターを起動
3. 「実行」をクリックするか、以下を実行：
   ```bash
   ./gradlew installDebug
   ```

## アクティビティ

- **MainActivity**: メインショッピングリストの表示と管理
- **SubActivity**: 商品情報の詳細表示
- **SettingsActivity**: アプリケーション設定と環境設定

## アプリケーションアイコン

アプリはカスタムアイコンを使用：
- 通常アイコン: `ic_icon`
- 丸形アイコン: `ic_icon_round`

## テーマ

アプリはMaterial Designテーマ（`Theme.BuyList`）を使用し、以下に対応：
- ライトモード
- ダークモード
- 端末のデフォルト設定

## テスト

テストを実行するには：
```bash
./gradlew test           # ユニットテスト
./gradlew connectedAndroidTest  # インストルメント化テスト
```

## リリース履歴

- **v1.0**: 初期リリース
- **v1.1**: バグ修正と改善
- **v1.2**: 最新リリース（追加機能を含む）

リリースビルドは`app/release/`ディレクトリにAABファイルとして保存されています。

## ライセンス

[ライセンス情報を記入してください]

## 作成者

[作成者情報を記入してください]

## サポート

問題、質問、機能リクエストはリポジトリのissueを作成してください。

## プライバシーと広告

このアプリはGoogle AdMobを使用して広告を表示します。データはGoogleのプライバシーポリシーとアプリのプライバシーステートメントに従って処理されます。

**Google AdMob アプリID**: `ca-app-pub-4419472517747863~9700877181`
