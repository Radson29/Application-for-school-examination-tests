import PageWrapper from 'components/HeadTeacher/PageWrapper';

function Home() {
  return (
    <PageWrapper>
      <header className="flex justify-between items-center pb-16">
        <div>
          <h1 className="text-4xl font-semibold">Home</h1>
          <p className="text-gray-500">Strona główna panelu dyrektora</p>
        </div>
        <div></div>
      </header>
    </PageWrapper>
  );
}

export default Home;
